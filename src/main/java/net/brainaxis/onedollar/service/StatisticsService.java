package net.brainaxis.onedollar.service;

import net.brainaxis.onedollar.entity.enums.StatRange;
import net.brainaxis.onedollar.entity.enums.StatType;
import net.brainaxis.onedollar.entity.stat.Insight;
import net.brainaxis.onedollar.entity.stat.InsightUnit;
import net.brainaxis.onedollar.entity.stat.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Service
public class StatisticsService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Statistics getStatistics(StatRange statRange, StatType statType) {
        LocalDateTime toDate = LocalDateTime.now();
        LocalDateTime fromDate = getFromDate(statRange, toDate, 1);

        Criteria actualDateCriteria =
                new Criteria("created")
                        .gte(Date.from(fromDate.atZone(ZoneId.systemDefault()).toInstant()))
                        .lt(Date.from(toDate.atZone(ZoneId.systemDefault()).toInstant()));

        LocalDateTime prevToDate = fromDate.minusDays(1);
        LocalDateTime prevFromDate = getFromDate(statRange, prevToDate, 1);

        Criteria prevDateCriteria =
                new Criteria("created")
                        .gte(Date.from(prevFromDate.atZone(ZoneId.systemDefault()).toInstant()))
                        .lt(Date.from(prevToDate.atZone(ZoneId.systemDefault()).toInstant()));


        Aggregation aggregation = newAggregation(
                match(new Criteria("created")
                        .gt(Date.from(prevToDate.atZone(ZoneId.systemDefault()).toInstant()))),

                Aggregation.group()
                        .sum(ConditionalOperators
                                .when(actualDateCriteria)
                                .then(getAggregationSumUpValue(statType))
                                .otherwise(0))
                        .as("currentCount")
                        .sum(ConditionalOperators
                                .when(prevDateCriteria)
                                .then(getAggregationSumUpValue(statType))
                                .otherwise(0))
                        .as("prevCount"),

                Aggregation.project()
                        .and("currentCount").as("unit")
                        .andExpression("((currentCount - prevCount) / prevCount) * 100").as("increaseInPercentage")
        );

        return mongoTemplate
                .aggregate(aggregation, getCollectionName(statType), Statistics.class)
                .getUniqueMappedResult();
    }

    public Insight getRevenueInsights(StatRange statRange, StatType statType) {
        List<InsightUnit> insightUnitList = getInsights(statRange, statType);

        List<String> dateList = insightUnitList.stream()
                .map(InsightUnit::getDate)
                .toList();

        List<Double> totalList = insightUnitList.stream()
                .map(InsightUnit::getTotal)
                .toList();

        return new Insight(dateList, totalList);
    }

    private List<InsightUnit> getInsights(StatRange statRange, StatType statType) {
        LocalDateTime toDate = LocalDateTime.now();
        LocalDateTime fromDate = getFromDate(statRange, toDate, 7);

        AggregationOperation matchOperation = Aggregation
                .match(
                        Criteria.where("created").gte(fromDate)
                );

        AggregationOperation projectOperation = Aggregation
                .project("created", "price")
                .and("created")
                .dateAsFormattedString(getTimeFormatString(statRange))
                .as("date");

        AggregationOperation groupOperation = getAggregationOperation(statType);

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                projectOperation,
                groupOperation
        );

        return mongoTemplate.aggregate(aggregation, getCollectionName(statType), InsightUnit.class).getMappedResults();
    }

    private AggregationOperation getAggregationOperation(StatType statType) {
        AggregationOperation groupOperation = null;

        switch (statType) {
            case Customer, Sales -> {
                groupOperation = Aggregation.group("date")
                        .count()
                        .as("total");
            }

            case Revenue -> {
                groupOperation = Aggregation.group("date")
                        .sum("price").as("total");
            }
        }
        return groupOperation;
    }

    private String getTimeFormatString(StatRange statRange) {
        switch (statRange) {
            case Daily -> {
                return "%Y-%m-%d";
            }

            case Monthly -> {
                return "%Y-%m";
            }

            case Yearly -> {
                return "%Y";
            }
        }

        throw new RuntimeException();
    }

    private Object getAggregationSumUpValue(StatType statType) {
        switch (statType) {
            case Customer, Sales -> {
                return 1;
            }

            case Revenue -> {
                return "price";
            }
        }

        throw new RuntimeException();
    }

    private String getCollectionName(StatType statType) {
        switch (statType) {
            case Customer -> {
                return "user";
            }

            case Revenue, Sales -> {
                return "userSubscription";
            }
        }

        throw new RuntimeException();
    }

    private LocalDateTime getFromDate(StatRange statRange, LocalDateTime toDate, int decreaseAmountUnit) {
        switch (statRange) {
            case Daily -> {
                return toDate.minusDays(decreaseAmountUnit);
            }
            case Monthly -> {
                return toDate.minusMonths(decreaseAmountUnit);
            }
            case Yearly -> {
                return toDate.minusYears(decreaseAmountUnit);
            }
        }

        throw new RuntimeException();
    }

}
