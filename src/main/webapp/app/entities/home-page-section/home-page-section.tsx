import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { Translate, getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './home-page-section.reducer';

export const HomePageSection = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const homePageSectionList = useAppSelector(state => state.homePageSection.entities);
  const loading = useAppSelector(state => state.homePageSection.loading);

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
      <h2 id="home-page-section-heading" data-cy="HomePageSectionHeading">
        <Translate contentKey="proficiencyTestingApp.homePageSection.home.title">Home Page Sections</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="proficiencyTestingApp.homePageSection.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/home-page-section/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="proficiencyTestingApp.homePageSection.home.createLabel">Create new Home Page Section</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {homePageSectionList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="proficiencyTestingApp.homePageSection.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('section')}>
                  <Translate contentKey="proficiencyTestingApp.homePageSection.section">Section</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('section')} />
                </th>
                <th className="hand" onClick={sort('type')}>
                  <Translate contentKey="proficiencyTestingApp.homePageSection.type">Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('type')} />
                </th>
                <th className="hand" onClick={sort('title')}>
                  <Translate contentKey="proficiencyTestingApp.homePageSection.title">Title</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('title')} />
                </th>
                <th className="hand" onClick={sort('text')}>
                  <Translate contentKey="proficiencyTestingApp.homePageSection.text">Text</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('text')} />
                </th>
                <th className="hand" onClick={sort('link')}>
                  <Translate contentKey="proficiencyTestingApp.homePageSection.link">Link</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('link')} />
                </th>
                <th className="hand" onClick={sort('fileRef')}>
                  <Translate contentKey="proficiencyTestingApp.homePageSection.fileRef">File Ref</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('fileRef')} />
                </th>
                <th className="hand" onClick={sort('icon')}>
                  <Translate contentKey="proficiencyTestingApp.homePageSection.icon">Icon</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('icon')} />
                </th>
                <th className="hand" onClick={sort('displayOrder')}>
                  <Translate contentKey="proficiencyTestingApp.homePageSection.displayOrder">Display Order</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('displayOrder')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="proficiencyTestingApp.homePageSection.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {homePageSectionList.map(homePageSection => (
                <tr key={`entity-${homePageSection.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/home-page-section/${homePageSection.id}`} variant="link" size="sm">
                      {homePageSection.id}
                    </Button>
                  </td>
                  <td>{homePageSection.section}</td>
                  <td>{homePageSection.type}</td>
                  <td>{homePageSection.title}</td>
                  <td>{homePageSection.text}</td>
                  <td>{homePageSection.link}</td>
                  <td>{homePageSection.fileRef}</td>
                  <td>{homePageSection.icon}</td>
                  <td>{homePageSection.displayOrder}</td>
                  <td>
                    <Translate contentKey={`proficiencyTestingApp.ContentStatus.${homePageSection.status}`} />
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/home-page-section/${homePageSection.id}`}
                        variant="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/home-page-section/${homePageSection.id}/edit`}
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
                        onClick={() => (globalThis.location.href = `/home-page-section/${homePageSection.id}/delete`)}
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
              <Translate contentKey="proficiencyTestingApp.homePageSection.home.notFound">No Home Page Sections found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default HomePageSection;
