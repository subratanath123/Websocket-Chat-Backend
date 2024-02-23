package net.brainaxis.onedollar.entity.stat;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Insight implements Serializable {

    private List<String> dateList;
    private List<Double> totalList;

    public Insight(List<String> dateList, List<Double> totalList) {
        this.dateList = dateList;
        this.totalList = totalList;
    }
}
