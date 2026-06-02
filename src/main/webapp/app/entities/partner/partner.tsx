import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { Translate, getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './partner.reducer';

export const Partner = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const partnerList = useAppSelector(state => state.partner.entities);
  const loading = useAppSelector(state => state.partner.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const { order } = sortState;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="partner-heading" data-cy="PartnerHeading">
        <Translate contentKey="proficiencyTestingApp.partner.home.title">Partners</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="proficiencyTestingApp.partner.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/partner/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="proficiencyTestingApp.partner.home.createLabel">Create new Partner</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {partnerList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="proficiencyTestingApp.partner.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('name')}>
                  <Translate contentKey="proficiencyTestingApp.partner.name">Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('link')}>
                  <Translate contentKey="proficiencyTestingApp.partner.link">Link</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('link')} />
                </th>
                <th className="hand" onClick={sort('logoRef')}>
                  <Translate contentKey="proficiencyTestingApp.partner.logoRef">Logo Ref</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('logoRef')} />
                </th>
                <th className="hand" onClick={sort('sortOrder')}>
                  <Translate contentKey="proficiencyTestingApp.partner.sortOrder">Sort Order</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sortOrder')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="proficiencyTestingApp.partner.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {partnerList.map(partner => (
                <tr key={`entity-${partner.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/partner/${partner.id}`} variant="link" size="sm">
                      {partner.id}
                    </Button>
                  </td>
                  <td>{partner.name}</td>
                  <td>{partner.link}</td>
                  <td>{partner.logoRef}</td>
                  <td>{partner.sortOrder}</td>
                  <td>
                    <Translate contentKey={`proficiencyTestingApp.ContentStatus.${partner.status}`} />
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/partner/${partner.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button as={Link as any} to={`/partner/${partner.id}/edit`} variant="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() => (globalThis.location.href = `/partner/${partner.id}/delete`)}
                        variant="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="proficiencyTestingApp.partner.home.notFound">No Partners found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Partner;
