export class User {
    userId: number = 0;
    firstname: string = "";
    lastname: string = "";
    password: string = "";
    email: string = "";
    wallet: number = 0;
    role: string = "";
    registrationDate: string = "";
    enabled:boolean = true;

    public constructor(init?: Partial<User>) {
        Object.assign(this, init);
    }

}