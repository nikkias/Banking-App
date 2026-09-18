export interface UserSession {
  username: string;
  roles: string[];
}

export interface StoredCredentials {
  username: string;
  authorization: string;
}
