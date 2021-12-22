const routes = [
  { path: '/', component: () => import('pages/login.vue') },
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '/home', component: () => import('pages/home.vue') },
      { path: '/details', component: () => import('pages/details.vue') },
      // { path: '/customer_management', component: () => import('pages/customer_management.vue') }, // 可跳页、调整视窗的的简单列表
      // { path: '/change_request', component: () => import('pages/change_request.vue') }, // 可跳页、调整视窗的的log列表
      { path: '/my_profile', component: () => import('pages/my_profile.vue') },
      { path: '/search', component: () => import('pages/search.vue'), alias: '/newsearch' },
      { path: '/cart', component: () => import('pages/cart.vue') },
      { path: '/order', component: () => import('pages/order.vue') },
      { path: '/my_shop', component: () => import('pages/my_shop.vue') },
      { path: '/shop', component: () => import('pages/shop.vue') },
    ]
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/Error404.vue')
  }
]

// // Always leave this as last one
// if (process.env.MODE !== 'ssr') {
//   routes.push({
//     path: '*',
//     component: () => import('pages/Error404.vue')
//   })
// }
// 
// export default routes

// default src/router/routes.js content:

export default routes