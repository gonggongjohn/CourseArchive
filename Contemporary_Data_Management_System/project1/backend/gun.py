import os
import gevent.monkey
import multiprocessing

gevent.monkey.patch_all()
bind = "0.0.0.0:5000"
workers = multiprocessing.cpu_count() * 2 +1
worker_class = 'gevent'
threads = 20
preload_app = True
reload = True
x_forwarded_for_header = 'X_FORWARDED-FOR'