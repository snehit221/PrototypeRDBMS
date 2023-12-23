package query.validator;

import query.core.CoreDatabaseQuery;
import query.request.CoreQueryOperationRequest;

public interface IQueryValidator<T extends CoreDatabaseQuery> {


    /**
     * @param coreQueryOperationRequest
     * @param coreDatabaseQuery
     * @implNote validates the respective CRUD query by executing the implementation class method
     */
    void validateQuery(CoreQueryOperationRequest coreQueryOperationRequest, T coreDatabaseQuery);

}


