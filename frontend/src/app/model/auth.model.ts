import {TagSelect} from './utils.model';

export class ConfirmTokenRequest {
  constructor(private userName: string, private code: string) {
  }
}

export class LoginRequest {
  constructor(private userName: string, private password: string, private confirmPassword: string) {
  }
}

export class RegisterRequest {
  constructor(private userName: string, private firstName: string, private lastName: string, private email: string,
              private password: string, private confirmPassword: string, private profileImageUrl: string) {}
}

export class ReplacePasswordRequest {
  constructor(private userName: string, private newPassword: string, private confirmPassword: string, private confirmation: string) {
  }
}

export class Role {
  constructor(public id: number, public name: string, public authorities: string[]) {
  }
}

export class Authority {
  constructor(public id: number, public name: string, public roles: string[]) {
  }
}




export enum HeaderType {
  AUTHORIZATION = 'Authorization',
  JWT_TOKEN = 'Jwt-Token'
}
