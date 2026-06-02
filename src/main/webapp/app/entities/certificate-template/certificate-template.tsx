import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { Translate, getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './certificate-template.reducer';

export const CertificateTemplate = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const certificateTemplateList = useAppSelector(state => state.certificateTemplate.entities);
  const loading = useAppSelector(state => state.certificateTemplate.loading);

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
      <h2 id="certificate-template-heading" data-cy="CertificateTemplateHeading">
        <Translate contentKey="proficiencyTestingApp.certificateTemplate.home.title">Certificate Templates</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="proficiencyTestingApp.certificateTemplate.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/certificate-template/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="proficiencyTestingApp.certificateTemplate.home.createLabel">Create new Certificate Template</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {certificateTemplateList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="proficiencyTestingApp.certificateTemplate.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('certificateType')}>
                  <Translate contentKey="proficiencyTestingApp.certificateTemplate.certificateType">Certificate Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('certificateType')} />
                </th>
                <th className="hand" onClick={sort('fileRef')}>
                  <Translate contentKey="proficiencyTestingApp.certificateTemplate.fileRef">File Ref</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('fileRef')} />
                </th>
                <th className="hand" onClick={sort('detectedFields')}>
                  <Translate contentKey="proficiencyTestingApp.certificateTemplate.detectedFields">Detected Fields</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('detectedFields')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {certificateTemplateList.map(certificateTemplate => (
                <tr key={`entity-${certificateTemplate.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/certificate-template/${certificateTemplate.id}`} variant="link" size="sm">
                      {certificateTemplate.id}
                    </Button>
                  </td>
                  <td>
                    <Translate contentKey={`proficiencyTestingApp.CertificateType.${certificateTemplate.certificateType}`} />
                  </td>
                  <td>{certificateTemplate.fileRef}</td>
                  <td>{certificateTemplate.detectedFields}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/certificate-template/${certificateTemplate.id}`}
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
                        to={`/certificate-template/${certificateTemplate.id}/edit`}
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
                        onClick={() => (globalThis.location.href = `/certificate-template/${certificateTemplate.id}/delete`)}
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
              <Translate contentKey="proficiencyTestingApp.certificateTemplate.home.notFound">No Certificate Templates found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default CertificateTemplate;
