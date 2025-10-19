interface RouteMeta {
    name: string
    path: string
}

export const MetaInfo: Record<string, RouteMeta> = {
    Spaces: {
        name: 'Spaces',
        path: '/spaces',
    },
    Images: {
        name: 'Images',
        path: '/images',
    },
    Login: {
        name: 'Login',
        path: '/login',
    },
    About: {
        name: 'About',
        path: '/about',
    },
    Space:{
        name: 'Space',
        path: '/space/:sid',
    },
    Test: {
        name: 'Test',
        path: '/test',
    },
}