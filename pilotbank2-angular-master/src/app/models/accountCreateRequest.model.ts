export class AccountCreateRequest {
  openedByCustomerId: number;
  accountType: string;
  balance: number; // this is fundingAmount
  isMainAccount: boolean;
}
