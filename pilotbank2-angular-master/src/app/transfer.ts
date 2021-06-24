export class Transfer{
    constructor(
        // todo: delete public modifiers
        public fromAccountId: number,
        public toAccountId: number,
        public transferAmount: number
    ){}
}