from django.shortcuts import render

# Home page view
def home(request):
    return render(request, 'core/index.html')

# About page view
def about(request):
    return render(request, 'core/about.html')

# Contact page view
def contact(request):
    return render(request, 'core/contact.html')

# Services page view
def services(request):
    return render(request, 'core/services.html')

# Blog page view
def blog(request):
    return render(request, 'core/blog.html')

# FAQ page view
def faq(request):
    return render(request, 'core/faq.html')
