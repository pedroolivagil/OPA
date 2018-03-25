package com.olivadevelop.persistence.interfaces;

import com.olivadevelop.persistence.entities.BasicEntity;

import java.util.List;

public interface ActionService {

    void preRun();

    void run();

    void postRun();

    <T extends BasicEntity> void run(T entity);

    <T extends BasicEntity> void run(List<T> entity);

}
