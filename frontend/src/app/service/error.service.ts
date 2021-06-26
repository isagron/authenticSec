import {Injectable} from '@angular/core';
import {AppError, AppErrorCodeMessage, MessageCode} from '../model/error.model';
import {HttpErrorResponse} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ErrorService {

  private errorCodeMapper = new AppErrorCodeMessage();

  constructor() {

  }

  public mapErrorResponse(errorResponse: HttpErrorResponse): AppError {
    const errorCodes: string[] = errorResponse.error.applicationErrorCode;
    if (!!errorCodes) {
      const cause = errorCodes
        .filter(code => !!this.errorCodeMapper.getMessage(code))
        .map((code) => new MessageCode(code, this.errorCodeMapper.getMessage(code)));
      const message = errorResponse.error.message;
      return new AppError(errorResponse.error.httpStatus, message, cause, errorResponse.error.timestamp);
    } else {
      return new AppError(errorResponse.error.httpStatus, 'Operation failed due to an unknown error, please contact your admin',
        [new MessageCode('Unknown', 'Unknown')], new Date());

    }
  }
}
