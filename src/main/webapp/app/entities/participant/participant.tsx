import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';

import { getEntities } from './participant.reducer';

export const Participant = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const participantList = useAppSelector(state => state.participant.entities);
  const loading = useAppSelector(state => state.participant.loading);
  const totalItems = useAppSelector(state => state.participant.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const { order } = paginationState;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="participant-heading" data-cy="ParticipantHeading">
        <Translate contentKey="proficiencyTestingApp.participant.home.title">Participants</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="proficiencyTestingApp.participant.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/participant/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="proficiencyTestingApp.participant.home.createLabel">Create new Participant</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {participantList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="proficiencyTestingApp.participant.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('uniqueIdentifier')}>
                  <Translate contentKey="proficiencyTestingApp.participant.uniqueIdentifier">Unique Identifier</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('uniqueIdentifier')} />
                </th>
                <th className="hand" onClick={sort('instituteName')}>
                  <Translate contentKey="proficiencyTestingApp.participant.instituteName">Institute Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('instituteName')} />
                </th>
                <th className="hand" onClick={sort('departmentName')}>
                  <Translate contentKey="proficiencyTestingApp.participant.departmentName">Department Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('departmentName')} />
                </th>
                <th className="hand" onClick={sort('email')}>
                  <Translate contentKey="proficiencyTestingApp.participant.email">Email</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('email')} />
                </th>
                <th className="hand" onClick={sort('additionalEmail')}>
                  <Translate contentKey="proficiencyTestingApp.participant.additionalEmail">Additional Email</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('additionalEmail')} />
                </th>
                <th className="hand" onClick={sort('address')}>
                  <Translate contentKey="proficiencyTestingApp.participant.address">Address</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('address')} />
                </th>
                <th className="hand" onClick={sort('shippingAddress')}>
                  <Translate contentKey="proficiencyTestingApp.participant.shippingAddress">Shipping Address</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('shippingAddress')} />
                </th>
                <th className="hand" onClick={sort('city')}>
                  <Translate contentKey="proficiencyTestingApp.participant.city">City</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('city')} />
                </th>
                <th className="hand" onClick={sort('state')}>
                  <Translate contentKey="proficiencyTestingApp.participant.state">State</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('state')} />
                </th>
                <th className="hand" onClick={sort('district')}>
                  <Translate contentKey="proficiencyTestingApp.participant.district">District</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('district')} />
                </th>
                <th className="hand" onClick={sort('zip')}>
                  <Translate contentKey="proficiencyTestingApp.participant.zip">Zip</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('zip')} />
                </th>
                <th className="hand" onClick={sort('region')}>
                  <Translate contentKey="proficiencyTestingApp.participant.region">Region</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('region')} />
                </th>
                <th className="hand" onClick={sort('phone')}>
                  <Translate contentKey="proficiencyTestingApp.participant.phone">Phone</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('phone')} />
                </th>
                <th className="hand" onClick={sort('mobile')}>
                  <Translate contentKey="proficiencyTestingApp.participant.mobile">Mobile</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('mobile')} />
                </th>
                <th className="hand" onClick={sort('affiliation')}>
                  <Translate contentKey="proficiencyTestingApp.participant.affiliation">Affiliation</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('affiliation')} />
                </th>
                <th className="hand" onClick={sort('networkTier')}>
                  <Translate contentKey="proficiencyTestingApp.participant.networkTier">Network Tier</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('networkTier')} />
                </th>
                <th className="hand" onClick={sort('siteType')}>
                  <Translate contentKey="proficiencyTestingApp.participant.siteType">Site Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('siteType')} />
                </th>
                <th className="hand" onClick={sort('fundingSource')}>
                  <Translate contentKey="proficiencyTestingApp.participant.fundingSource">Funding Source</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('fundingSource')} />
                </th>
                <th className="hand" onClick={sort('testingVolume')}>
                  <Translate contentKey="proficiencyTestingApp.participant.testingVolume">Testing Volume</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('testingVolume')} />
                </th>
                <th className="hand" onClick={sort('pepfarId')}>
                  <Translate contentKey="proficiencyTestingApp.participant.pepfarId">Pepfar Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('pepfarId')} />
                </th>
                <th className="hand" onClick={sort('latitude')}>
                  <Translate contentKey="proficiencyTestingApp.participant.latitude">Latitude</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('latitude')} />
                </th>
                <th className="hand" onClick={sort('longitude')}>
                  <Translate contentKey="proficiencyTestingApp.participant.longitude">Longitude</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('longitude')} />
                </th>
                <th className="hand" onClick={sort('labDirectorName')}>
                  <Translate contentKey="proficiencyTestingApp.participant.labDirectorName">Lab Director Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('labDirectorName')} />
                </th>
                <th className="hand" onClick={sort('labDirectorEmail')}>
                  <Translate contentKey="proficiencyTestingApp.participant.labDirectorEmail">Lab Director Email</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('labDirectorEmail')} />
                </th>
                <th className="hand" onClick={sort('contactPersonName')}>
                  <Translate contentKey="proficiencyTestingApp.participant.contactPersonName">Contact Person Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('contactPersonName')} />
                </th>
                <th className="hand" onClick={sort('contactPersonEmail')}>
                  <Translate contentKey="proficiencyTestingApp.participant.contactPersonEmail">Contact Person Email</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('contactPersonEmail')} />
                </th>
                <th className="hand" onClick={sort('contactPersonPhone')}>
                  <Translate contentKey="proficiencyTestingApp.participant.contactPersonPhone">Contact Person Phone</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('contactPersonPhone')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="proficiencyTestingApp.participant.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th>
                  <Translate contentKey="proficiencyTestingApp.participant.country">Country</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {participantList.map(participant => (
                <tr key={`entity-${participant.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/participant/${participant.id}`} variant="link" size="sm">
                      {participant.id}
                    </Button>
                  </td>
                  <td>{participant.uniqueIdentifier}</td>
                  <td>{participant.instituteName}</td>
                  <td>{participant.departmentName}</td>
                  <td>{participant.email}</td>
                  <td>{participant.additionalEmail}</td>
                  <td>{participant.address}</td>
                  <td>{participant.shippingAddress}</td>
                  <td>{participant.city}</td>
                  <td>{participant.state}</td>
                  <td>{participant.district}</td>
                  <td>{participant.zip}</td>
                  <td>{participant.region}</td>
                  <td>{participant.phone}</td>
                  <td>{participant.mobile}</td>
                  <td>{participant.affiliation}</td>
                  <td>{participant.networkTier}</td>
                  <td>{participant.siteType}</td>
                  <td>{participant.fundingSource}</td>
                  <td>{participant.testingVolume}</td>
                  <td>{participant.pepfarId}</td>
                  <td>{participant.latitude}</td>
                  <td>{participant.longitude}</td>
                  <td>{participant.labDirectorName}</td>
                  <td>{participant.labDirectorEmail}</td>
                  <td>{participant.contactPersonName}</td>
                  <td>{participant.contactPersonEmail}</td>
                  <td>{participant.contactPersonPhone}</td>
                  <td>
                    <Translate contentKey={`proficiencyTestingApp.Status.${participant.status}`} />
                  </td>
                  <td>{participant.country ? <Link to={`/country/${participant.country.id}`}>{participant.country.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/participant/${participant.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/participant/${participant.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        variant="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() =>
                          (globalThis.location.href = `/participant/${participant.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
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
              <Translate contentKey="proficiencyTestingApp.participant.home.notFound">No Participants found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={participantList && participantList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default Participant;
