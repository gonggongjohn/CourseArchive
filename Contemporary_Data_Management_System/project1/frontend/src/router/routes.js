
const routes = [
  {
    path: '/',
    component: () => import('layouts/SignLayout.vue'),
    children: [
      { path: '', component: () => import('pages/Login.vue') },
      { path: '/register', component: () => import('pages/Register.vue') }
    ]
  },

  {
    path: '/board',
    component: () => import('src/layouts/InGameLayout.vue'),
    children: [
      { path: '', component: () => import('src/pages/Profile.vue') },
      { path: '/game', component: () => import('/src/pages/Game.vue')},
      { path: '/container', component: () => import('pages/Container.vue') },
      { path: '/market', component: () => import('pages/Market.vue') },
      { path: '/admin', component: () => import('pages/Admin.vue') }
    ]
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/Error404.vue')
  }
]

export default routes
