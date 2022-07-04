package com.github.ogaltsov.amzscouttesttask.model;

import lombok.Data;

import java.util.TreeMap;

@Data
public class TimeSeries {

    TreeMap<Long, Integer> timeToRequestCount = new TreeMap<>();


    public int getRequestCountAfterTime(long time) {

        return timeToRequestCount.tailMap(time)
            .values()
            .stream()
            .mapToInt(it -> it)
            .sum();
    }

    public int getSeriesCountBeforeTime(long time) {
        return timeToRequestCount.headMap(time)
            .values()
            .size();
    }

    public int getSeriesCount() {
        return timeToRequestCount.size();
    }

    public void addRequestAtTime(long time) {
        timeToRequestCount.put(time, timeToRequestCount.getOrDefault(time, 0) + 1);
    }

    public void removeBeforeTime(long time) {
        timeToRequestCount.headMap(time).keySet().forEach( it -> timeToRequestCount.remove(it) );
    }
}
