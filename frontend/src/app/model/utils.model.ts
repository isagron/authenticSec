export class Page<T> {
  constructor(public content: T[], public pageNumber: number, public pageSize: number, public totalElements: number) {
  }

  public getNumberOfPages(): number {
    return Math.ceil(this.totalElements / this.pageSize);
  }
}

export class TagSelect {
  constructor(public tag: string, public select: boolean) {
  }
}
