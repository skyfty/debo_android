package com.qcwl.debo.widget.sortlistview;

import com.qcwl.debo.model.ContactsBean;
import com.qcwl.debo.model.MobileUserBean;

import java.util.Comparator;

/**
 *
 * @author xiaanming
 *
 */
public class PinyinComparator2 implements Comparator<MobileUserBean> {

    public int compare(MobileUserBean o1, MobileUserBean o2) {
        if (o1.getSortLetters().equals("@")
                || o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")
                || o2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }

}