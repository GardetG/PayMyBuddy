export class User {
    userId: number = 0;
    firstname: string = "";
    lastname: string = "";
    password: string = "";
    email: string = "";
    wallet: number = 0;
    role: string = "";
    registrationDate: string = "";

    public constructor(init?: Partial<User>) {
        Object.assign(this, init);
    }

}