package org.skfiy.typhon.repository;

import java.util.List;
import java.util.Map;

import org.skfiy.typhon.domain.Incident;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */

public interface IncidentRepository {
    /**
     *
     * @param incident
     */
    void save(Incident incident);

    /**
     *
     * @param id
     */
    void delete(long id);

    /**
     *
     * @param uid
     * @return
     */
    List<Incident> findByUid(long uid);

    /**
     *
     * @param uid
     * @param eventName
     * @param data
     * @return
     */
    @Deprecated
    boolean findByData(int uid, String eventName, String data);

    /**
     *
     * @param uid
     * @param eventName
     * @return
     */
    List<String> findData(int uid, String eventName);

    /**
     * 
     * @param data
     * @return
     */
    int findByData(String data);

    /**
     * 
     * @param uid
     * @param eventName
     * @return Map<pid,data>
     */
    Map<Integer, String> findPidData(int uid, String eventName);

}
