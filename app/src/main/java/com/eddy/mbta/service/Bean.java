package com.eddy.mbta.service;

import com.eddy.mbta.json.Schedule;

import java.util.List;
import java.util.Set;

public class Bean {

    List<Schedule> list;

    Set<Integer> set;

    public List<Schedule> getList() {
        return list;
    }

    public void setList(List<Schedule> list) {
        this.list = list;
    }

    public Set<Integer> getSet() {
        return set;
    }

    public void setSet(Set<Integer> set) {
        this.set = set;
    }
}
