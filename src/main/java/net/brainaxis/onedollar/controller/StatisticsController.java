package net.brainaxis.onedollar.controller;

import net.brainaxis.onedollar.entity.enums.StatRange;
import net.brainaxis.onedollar.entity.enums.StatType;
import net.brainaxis.onedollar.entity.stat.Insight;
import net.brainaxis.onedollar.entity.stat.Statistics;
import net.brainaxis.onedollar.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("statistics")
@RequestMapping("/statistics")
@CrossOrigin(origins = {"https://react-next-js-with-type-script-admin.vercel.app/", "https://one-dollar-customer-frontend.vercel.app/", "http://localhost:3000" }, allowCredentials = "true", allowedHeaders = "*")
public class StatisticsController {

    private final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping
    public Statistics showStatistics(@RequestParam StatType statType, @RequestParam StatRange statRange) {
        return statisticsService.getStatistics(statRange, statType);
    }

    @GetMapping(value = "insights")
    public Insight showInsights(@RequestParam StatType statType, @RequestParam StatRange statRange) {
        return statisticsService.getRevenueInsights(statRange, statType);
    }


}
