export class UserDetails {
  constructor(private firstName: string, private lastName: string, private userName: string, private email: string,
              private role: string, private isLock: boolean, private isActive: boolean) {
  }

}


export class User {
  constructor(public id: number, public userId: string, public firstName: string, public lastName: string, public userName: string,
              public email: string, public profileImageUrl: string, public lastLoginDate: Date, public joinDate: Date,
              public role: string, public authorities: string[], public isLock: boolean, public isActive: boolean,
              public emailVerification: boolean, private needToReplacePassword: boolean,
              public successfulLogin: number, public failureLogin: number, public lastTimeRenewPassword: Date) {
  }

}


