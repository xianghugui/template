package com.base.web.dao;

/**
 * @author
 */
public interface CRUMapper<Po, Pk> extends InsertMapper<Po>, QueryMapper<Po, Pk>, UpdateMapper<Po> {
}
