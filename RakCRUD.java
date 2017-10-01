package com.reaper.rick.raklibrary;

import java.util.ArrayList;

/**
 * Created by Reaper on 9/26/2017.
 */

public interface RakCRUD {
    public boolean save(Object o);
    public boolean delete(Object o);
    public boolean update(Object o);
    public boolean where(String... where);
    public boolean one(Object o);
    public int count(Object o);
}
