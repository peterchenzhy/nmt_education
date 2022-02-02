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

export interface User {
  name: string,
  logInUser: any,
}


export interface StudentQueryParam {
    name?: string;
    pageNo?: number;
    pageSize?: number;
    phone?: string;
}

export interface StudentAccountQueryParam {
    studentId?: number;
    pageNo?: number;
    pageSize?: number;
}

export interface TeacherQueryParam {
    name?: string;
    pageNo?: number;
    pageSize?: number;
    phone?: string;
}

export interface TeacherCourseQueryParam {
    teacherId?: number;
    pageNo?: number;
    pageSize?: number;
}

export interface CourseQueryParam {
    campus?: number;
    courseSubject?: number;
    courseType?: number;
    grade?: number;
    startDate?: string;
    endDate?: string;
    year?: number;
    season?: number;
    pageNo?: number;
    pageSize?: number;
}

export interface RegisterSummaryQueryParam {
    courseSubject?: number;
    courseType?: number;
    grade?: number;
    startDate?: string;
    endDate?: string;
    registerStartDate?: string;
    registerEndDate?: string;
    signIn?: number;
    pageNo?: number;
    pageSize?: number;
    year?: number;
    season?: number;
    userCode?: number;
    campus?:number;
}

export interface FeeStatisticsQueryParam {
    campus?: number,
    feeFlowType?: number,
    startDate?: string;
    endDate?: string;
    pageNo?: number;
    pageSize?: number;
    year?: number;
    season?: number;
    userCode? :number;
}

export interface RegisterQueryParam {
    studentId?: number;
    courseSubject?: number;
    courseType?: number;
    signInDateStart?: string;
    signInDateEnd?: string;
    grade?: number;
    pageNo?: number;
    pageSize?: number;
    orderCode?: string;
    year?: number;
    season?: number;
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
export interface FeeSummary {
  pay?: string;
  actuallyHandIn?: string;
  amountSummary?: string;
  refund?: string;
  teacherPay?: string;
  startDate?: string;
  endDate?: string;
  refund2Account?: string;
  registerStudentCount?: number;
}

export interface RegisterSummaryTotal {
  totalCount?: number;
  signInCount?: number;
  unSignInCount?: number;
  registerStudentCount?: number;
  registerCount?: number;
}
