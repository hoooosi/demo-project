import useDataStore from '@/stores/dataStore'
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
    if (res.code === 40100) {
      localStorage.removeItem('user')
      useDataStore().user = undefined
    }
    return Promise.reject(new Error(res.message))
  },
  (error) => {
    return Promise.reject(error)
  }
)

const UserApi = {
  SERVER_NAME: '/user-service',
  //========================================================================================================================
  login: async (data: Req.LoginReq): Promise<BaseRes<void>> =>
    service.post(`${UserApi.SERVER_NAME}/login`, data),
  register: async (data: Req.RegisterReq): Promise<BaseRes<void>> =>
    service.post(`${UserApi.SERVER_NAME}/register`, data),
  logout: async (): Promise<BaseRes<void>> => service.post(`${UserApi.SERVER_NAME}/logout`),
  auth: async (): Promise<BaseRes<VO.UserVO>> => {
    const res = await service.get(`${UserApi.SERVER_NAME}/auth`)
    localStorage.setItem('user', JSON.stringify(res.data))
    useDataStore().user = res.data
    return res as any
  },
  get: async (userId: string): Promise<BaseRes<VO.UserVO>> =>
    service.get(`${UserApi.SERVER_NAME}`, { params: { userId } }),
}

const CompositeApi = {
  SERVER_NAME: '/composite-service',
  //========================================================================================================================
  createSpace: async (req: Req.AddSpaceReq): Promise<BaseRes<void>> =>
    service.post(`${CompositeApi.SERVER_NAME}/space`, req),
  pageSpaceByPublic: async (params: Req.QuerySpaceReq & { notShowJoined?: boolean }): Promise<BaseRes<Page<VO.SpaceVO>>> =>
    service.get(`${CompositeApi.SERVER_NAME}/space/page`, { params: { ...params } }),
  pageSpaceByOperator: async (params: Req.QuerySpaceReq & { isOwner?: boolean }): Promise<BaseRes<Page<VO.SpaceVO>>> =>
    service.get(`${CompositeApi.SERVER_NAME}/space/page/u`, { params: { ...params } }),
  getSpace: async (spaceId: string): Promise<BaseRes<VO.SpaceVO>> =>
    service.get(`${CompositeApi.SERVER_NAME}/space/${spaceId}`),
  getSpacePermission: async (spaceId: string): Promise<BaseRes<string>> =>
    service.get(`${CompositeApi.SERVER_NAME}/space/permission/${spaceId}`),
  getSpacePermissions: async (ids: string[]): Promise<BaseRes<Map<string, string>>> =>
    service.get(`${CompositeApi.SERVER_NAME}/space/permission`, { params: { ids } }),
  listJoinedSpaceIds: async (): Promise<BaseRes<string[]>> =>
    service.get(`${CompositeApi.SERVER_NAME}/space/list/u`),
  editSpace: async (req: Req.EditSpaceReq): Promise<BaseRes<void>> =>
    service.put(`${CompositeApi.SERVER_NAME}/space`, req),
  deleteSpace: async (spaceId: string): Promise<BaseRes<void>> =>
    service.delete(`${CompositeApi.SERVER_NAME}/space/${spaceId}`),
  //========================================================================================================================
  listMember: async (spaceId: string): Promise<BaseRes<VO.MemberVo[]>> =>
    service.get(`${CompositeApi.SERVER_NAME}/member/list`, { params: { spaceId } }),
  editMember: async (req: Req.EditMemberReq): Promise<BaseRes<void>> =>
    service.put(`${CompositeApi.SERVER_NAME}/member`, req),
  exitSpace: async (spaceId: string): Promise<BaseRes<void>> =>
    service.post(`${CompositeApi.SERVER_NAME}/member/quit/${spaceId}`),
  removeMember: async (memberId: string): Promise<BaseRes<void>> =>
    service.delete(`${CompositeApi.SERVER_NAME}/member/${memberId}`),
  //========================================================================================================================
  pageApplicationBySpace: async (params: { status?: string }, spaceId: string): Promise<BaseRes<VO.ApplicationVO[]>> =>
    service.get(`${CompositeApi.SERVER_NAME}/application/list/${spaceId}`, { params: { ...params } }),
  pageApplicationByOperator: async (params: { status?: string }): Promise<BaseRes<VO.ApplicationVO[]>> =>
    service.get(`${CompositeApi.SERVER_NAME}/application/list`, { params: { ...params } }),
  apply: async (spaceId: string): Promise<BaseRes<void>> =>
    service.post(`${CompositeApi.SERVER_NAME}/application/apply/${spaceId}`),
  invite: async (spaceId: string, userId: string): Promise<BaseRes<void>> =>
    service.post(`${CompositeApi.SERVER_NAME}/application/invite/${spaceId}/${userId}`),
  handleApplication: async (req: Req.HandleApplicationReq): Promise<BaseRes<void>> =>
    service.put(`${CompositeApi.SERVER_NAME}/application/handle`, req),
}

const ImageApi = {
  SERVER_NAME: '/image-service',
  //========================================================================================================================
  view: async (itemId: string): Promise<BaseRes<VO.ImageVO>> =>
    service.get(`${ImageApi.SERVER_NAME}/image/view/${itemId}`),
  viewThumbnail: async (itemId: string): Promise<BaseRes<VO.ImageVO>> =>
    service.get(`${ImageApi.SERVER_NAME}/image/view/thumbnail/${itemId}`),
  //========================================================================================================================
  pageByPublic: async (params: Req.QueryImgReq): Promise<BaseRes<Page<VO.ImageVO>>> =>
    service.get(`${ImageApi.SERVER_NAME}/image/page`, { params: { ...params } }),
  pageByOperator: async (params: Req.QueryImgReq & { spaceId?: string }): Promise<BaseRes<Page<VO.ImageVO>>> =>
    service.get(`${ImageApi.SERVER_NAME}/image/page/u`, { params: { ...params } }),
  pageBySpace: async (params: Req.QueryImgReq & { spaceId: string, userId?: string }): Promise<BaseRes<Page<VO.ImageVO>>> =>
    service.get(`${ImageApi.SERVER_NAME}/image/page/s`, { params: { ...params } }),
  listTags: async (): Promise<BaseRes<string[]>> =>
    service.get(`${ImageApi.SERVER_NAME}/image/tags`),
  //========================================================================================================================
  upload: async (file: File, spaceId: string): Promise<BaseRes<void>> => {
    const formData = new FormData()
    formData.append('file', file)
    return service.post(`${ImageApi.SERVER_NAME}/image/upload/${spaceId}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  checkUpload(spaceId: string, data: {
    filename: string;
    contentType: string;
    md5: string;
    size: number;
  }) {
    return service.post(`${ImageApi.SERVER_NAME}/image/upload/${spaceId}`, data);
  },
  edit: async (req: Req.EditImageReq): Promise<BaseRes<void>> =>
    service.put(`${ImageApi.SERVER_NAME}/image`, req),
  convert: async (idxId: string, contentType: string): Promise<BaseRes<void>> =>
    service.post(`${ImageApi.SERVER_NAME}/image/convert`, null, { params: { idxId, contentType } }),
  delete: async (idxId: string): Promise<BaseRes<void>> =>
    service.delete(`${ImageApi.SERVER_NAME}/image/${idxId}`),
  deleteBatch: async (ids: string[]): Promise<BaseRes<void>> =>
    service.delete(`${ImageApi.SERVER_NAME}/image/batch?ids=${ids.join('&ids=')}`),
}



export { UserApi, CompositeApi as SumApi, ImageApi }
