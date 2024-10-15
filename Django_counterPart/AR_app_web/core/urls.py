from django.urls import path
from . import views

urlpatterns = [
    path('', views.home, name='home'),
    path('about/', views.about, name='about'),
    path('contact/', views.contact, name='contact'),
    path('services/', views.services, name='services'),
    path('blog/', views.blog, name='blog'),
    path('faq/', views.faq, name='faq'),
    path('gallery/', views.gallery, name='gallery'),
    path('team/', views.team, name='team'),
    path('pricing/', views.pricing, name='pricing'),
    path('testimonials/', views.testimonials, name='testimonials'),
    path('terms-of-service/', views.terms_of_service, name='terms_of_service'),
]
