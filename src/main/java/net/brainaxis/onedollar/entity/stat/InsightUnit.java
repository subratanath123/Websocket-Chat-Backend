package net.brainaxis.onedollar.entity.stat;

import lombok.Data;

import java.io.Serializable;

@Data
public class InsightUnit implements Serializable {

    private String date;
    private double total;

}
