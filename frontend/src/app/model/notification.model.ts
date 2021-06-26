import {Authority, Role} from './auth.model';
import {User} from './user.model';

export enum MessageType {
  CREATE = 'CREATE',
  UPDATE = 'UPDATE',
  DELETE = 'DELETE'
}

export class AuthorityMessage {
  constructor(public messageType: MessageType, public payload: Authority) {
  }
}

export class RoleMessage {
  constructor(public messageType: MessageType, public payload: Role) {
  }
}

export class UserMessage {
  constructor(public messageType: MessageType, public payload: User) {
  }
}
