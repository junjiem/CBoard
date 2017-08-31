package org.cboard.dao;

import org.cboard.pojo.DashboardShareBoard;
import org.springframework.stereotype.Repository;

/**
 * Created by jintian on 2017/8/31.
 */
@Repository
public interface ShareBoardDao {

    int save(DashboardShareBoard shareBoard);

    DashboardShareBoard getOpenShareBoard(String uid);
}
