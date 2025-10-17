import axios from 'axios'
import qs from 'qs';

const service = axios.create({
  baseURL: '/api',
  paramsSerializer: (params) => {
    return qs.stringify(params, {
      arrayFormat: 'repeat',
      encode: true,
      skipNulls: true
    });
  }
})

// Global request interceptor
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token)
      config.headers.token = token
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor
service.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) return res
    return Promise.reject(new Error(res.message))
  },
  (error) => {
    return Promise.reject(error)
  }
)

const UserApi = {
  checkCode: async (): Promise<Resp<VO.CheckCodeVO>> =>
    service.get(`/user/checkCode`),
  login: async (data: DTO.LoginDTO): Promise<Resp<string>> =>
    service.post(`/user/login`, data),
  register: async (data: DTO.RegisterDTO): Promise<Resp<void>> =>
    service.post(`/user/register`, data),
  me: async (): Promise<Resp<VO.UserVO>> =>
    service.get(`/user/me`)
}

const MeetingApi = {
  getCurrentMeeting: async (): Promise<Resp<string>> =>
    service.get(`/meeting/getCurrentMeeting`),
  loadMeeting: async (dto: DTO.LoadMeetingDTO): Promise<Resp<Page<VO.MeetingVO>>> =>
    service.get(`/meeting/loadMeeting`, { params: dto }),
  quickMeeting: async (dto: DTO.QuickStartMeetingDTO): Promise<Resp<void>> =>
    service.post(`/meeting/quickMeeting`, dto),
  preJoinMeeting: async (dto: DTO.PreJoinMeetingDTO): Promise<Resp<void>> =>
    service.post(`/meeting/preJoinMeeting`, dto),
  joinMeeting: async (dto: DTO.JoinMeetingDTO): Promise<Resp<string[]>> =>
    service.post(`/meeting/joinMeeting`, dto),
  leaveMeeting: async (): Promise<Resp<void>> =>
    service.get(`/meeting/leaveMeeting`),
}


export { UserApi, MeetingApi }
