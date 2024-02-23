package net.brainaxis.onedollar.entity.stat;

import lombok.Data;
import net.brainaxis.onedollar.entity.enums.StatRange;
import net.brainaxis.onedollar.entity.enums.StatType;

import java.io.Serializable;

@Data
public class Statistics implements Serializable {

    private StatType statType;
    private StatRange statRange;
    private double unit;
    private double increaseInPercentage;

}
