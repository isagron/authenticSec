import {HttpClient} from '@angular/common/http';

export class ErrorDto {
  constructor(private status: string, private applicationErrorCode: string,
              private message: string, private phrase: string, private timeStamp: Date) {
  }
}

export class AppError {
  constructor(private status: string, public message: string, public cause: MessageCode[], private timeStamp: Date) {
  }
}

export class MessageCode {
  constructor(public code: string, public message: string) {
  }
}

export class AppErrorCodeMessage {

  public errorCodeToMessageMap = new Map();

  constructor() {
    this.errorCodeToMessageMap.set('user_name_already_exist', 'User name already exist, please choose another one');
    this.errorCodeToMessageMap.set('user_not_exit', 'User not exist');
    this.errorCodeToMessageMap.set('mail_already_exist', 'This email already exist, please choose another one');
    this.errorCodeToMessageMap.set('confirmation_token_not_exist', 'Invalid code, please insert the code you receive in your mail');
    this.errorCodeToMessageMap.set('confirmation_token_invalid', 'Invalid confirmation code');
    this.errorCodeToMessageMap.set('user.save_image', 'Failed to upload image');
    this.errorCodeToMessageMap.set('user.image_not_found', 'Failed to upload image');
    this.errorCodeToMessageMap.set('security.authentication.token.verification', 'Token can not be verified');
    this.errorCodeToMessageMap.set('security.authentication.account.disable', 'Your account has been disabled. If this is an error, please contact administration');
    this.errorCodeToMessageMap.set('security.authentication.forbidden', 'You need to login to access this page');
    this.errorCodeToMessageMap.set('security.authentication.access-denied', 'ou do not have permission to access this page');
    this.errorCodeToMessageMap.set('security.authentication.not-enough-permission', 'You do not have enough permission');
    this.errorCodeToMessageMap.set('security.authentication.invalid-credentials', 'User name / password incorrect. Please try again');
    this.errorCodeToMessageMap.set('security.authentication.account-lock', 'Your account has been locked. Please contact administration');
    this.errorCodeToMessageMap.set('security.authentication.internal-server-error', 'An error occurred while processing the request');
    this.errorCodeToMessageMap.set('security.authentication.method-not-allowed', 'This request method is not allowed on this endpoint');
    this.errorCodeToMessageMap.set('missing_verification_code', 'Please insert the code sent to your mail');
  }

  public getMessage(code: string): string {
    return this.errorCodeToMessageMap.get(code);
  }

}
