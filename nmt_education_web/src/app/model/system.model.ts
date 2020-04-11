export interface SysEnum {
    label: string,
    value: any,
    dbConfig: boolean,
    type: string,
    typeCode: string,
    typeDesc: string,
    icon: string
}

export interface SysEnums {
    courseClassification: SysEnum[],
    registrationStatus: SysEnum[],
    courseType: SysEnum[],
    courseSubject: SysEnum[],
    campus: SysEnum[],
    grade: SysEnum[],
    season: SysEnum[],
    courseStatus: SysEnum[],
    feeType: SysEnum[],
    feeStatus: SysEnum[]
}

export interface StudentQueryParam {
    name?: string;
    pageNo?: number;
    pageSize?: number;
    phone?: string;
}

export interface ResponseData {
    endRow?: number;
    hasNextPage?: boolean;
    hasPreviousPage?: boolean;
    isFirstPage?: boolean;
    isLastPage?: boolean;
    list?: any;
    startRow?: number;
    total?: number;
}