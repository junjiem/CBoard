package org.cboard.dataprovider.config;

import java.util.Iterator;

/**
 * Created by zyong on 2017/4/24.
 */
public abstract class ConfigComponent {

    public void add(ConfigComponent ConfigComponent) {
        //throw new UnsupportedOperationException();
    }

    public void remove(ConfigComponent ConfigComponent) {
        //throw new UnsupportedOperationException();
    }

    public Iterator<ConfigComponent> getIterator() {
        //throw new UnsupportedOperationException();
        return null;
    }

    public String getName() {
        //throw new UnsupportedOperationException();
        return null;
    }

    public String getDescription() {
        //throw new UnsupportedOperationException();
        return null;
    }

    public void print() {
        //throw new UnsupportedOperationException();
    }
}
