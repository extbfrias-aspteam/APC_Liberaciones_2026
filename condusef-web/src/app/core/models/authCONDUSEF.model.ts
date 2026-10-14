export interface UserCONDUSEF{
    username: string;
    password: string;
}

export interface AuthuserCONDUSEF {
    token_access: string;
    username: string;
}

export interface AuthuserCONDUSEFResponse {
    msg: string;
    user: AuthuserCONDUSEF;
    message: string;
}