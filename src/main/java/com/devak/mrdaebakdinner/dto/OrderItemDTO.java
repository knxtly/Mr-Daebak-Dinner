package com.devak.mrdaebakdinner.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderItemDTO {
    private Map<String, Integer> orderItems = new HashMap<>();

    // wine
    public Integer getWine() {
        if (orderItems == null) return 0;
        return orderItems.getOrDefault("wine", 0);
    }

    public void setWine(Integer wine) {
        if (orderItems != null) {
            orderItems.put("wine", wine);
        }
    }

    // steak
    public Integer getSteak() {
        if (orderItems == null) return 0;
        return orderItems.getOrDefault("steak", 0);
    }

    public void setSteak(Integer steak) {
        if (orderItems != null) {
            orderItems.put("steak", steak);
        }
    }

    // coffee_cup
    public Integer getCoffee_cup() {
        if (orderItems == null) return 0;
        return orderItems.getOrDefault("coffee_cup", 0);
    }

    public void setCoffee_cup(Integer coffee_cup) {
        if (orderItems != null) {
            orderItems.put("coffee_cup", coffee_cup);
        }
    }

    // coffee_pot
    public Integer getCoffee_pot() {
        if (orderItems == null) return 0;
        return orderItems.getOrDefault("coffee_pot", 0);
    }

    public void setCoffee_pot(Integer coffee_pot) {
        if (orderItems != null) {
            orderItems.put("coffee_pot", coffee_pot);
        }
    }

    // salad
    public Integer getSalad() {
        if (orderItems == null) return 0;
        return orderItems.getOrDefault("salad", 0);
    }

    public void setSalad(Integer salad) {
        if (orderItems != null) {
            orderItems.put("salad", salad);
        }
    }

    // eggscramble
    public Integer getEggscramble() {
        if (orderItems == null) return 0;
        return orderItems.getOrDefault("eggscramble", 0);
    }

    public void setEggscramble(Integer eggscramble) {
        if (orderItems != null) {
            orderItems.put("eggscramble", eggscramble);
        }
    }

    // bacon
    public Integer getBacon() {
        if (orderItems == null) return 0;
        return orderItems.getOrDefault("bacon", 0);
    }

    public void setBacon(Integer bacon) {
        if (orderItems != null) {
            orderItems.put("bacon", bacon);
        }
    }

    // bread
    public Integer getBread() {
        if (orderItems == null) return 0;
        return orderItems.getOrDefault("bread", 0);
    }

    public void setBread(Integer bread) {
        if (orderItems != null) {
            orderItems.put("bread", bread);
        }
    }

    // baguette
    public Integer getBaguette() {
        if (orderItems == null) return 0;
        return orderItems.getOrDefault("baguette", 0);
    }

    public void setBaguette(Integer baguette) {
        if (orderItems != null) {
            orderItems.put("baguette", baguette);
        }
    }

    // champagne
    public Integer getChampagne() {
        if (orderItems == null) return 0;
        return orderItems.getOrDefault("champagne", 0);
    }

    public void setChampagne(Integer champagne) {
        if (orderItems != null) {
            orderItems.put("champagne", champagne);
        }
    }
}
