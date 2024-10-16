from django.shortcuts import render

# Existing views
def home(request):
    return render(request, 'core/index.html')

def about(request):
    return render(request, 'core/about.html')

def contact(request):
    return render(request, 'core/contact.html')

def services(request):
    return render(request, 'core/services.html')

def blog(request):
    return render(request, 'core/blog.html')

def faq(request):
    return render(request, 'core/faq.html')

# New views
def gallery(request):
    return render(request, 'core/gallery.html')

def team(request):
    return render(request, 'core/team.html')

def pricing(request):
    return render(request, 'core/pricing.html')

def testimonials(request):
    return render(request, 'core/testimonials.html')

def terms_of_service(request):
    return render(request, 'core/terms_of_service.html')
