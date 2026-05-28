import os
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from florra.models import CustomerUser

old_email = "test@test.com"
new_email = "asskrvsdhana1215@gmail.com"

try:
    user = CustomerUser.objects.get(email=old_email)
    user.email = new_email
    user.save()
    print(f"Successfully updated email from {old_email} to {new_email}")
except CustomerUser.DoesNotExist:
    print(f"User with email {old_email} not found in CustomerUser.")
    
    # Try Django's standard User model just in case
    from django.contrib.auth.models import User
    try:
        user2 = User.objects.get(email=old_email)
        user2.email = new_email
        user2.username = new_email
        user2.save()
        print(f"Successfully updated email in auth.User from {old_email} to {new_email}")
    except User.DoesNotExist:
        print(f"User with email {old_email} not found in standard User.")
