from rest_framework.authentication import BaseAuthentication
from rest_framework.exceptions import AuthenticationFailed
from .models import AdminToken

class AdminTokenAuthentication(BaseAuthentication):
    def authenticate(self, request):
        auth_header = request.headers.get('Authorization')
        if not auth_header:
            return None

        if not auth_header.startswith('Token '):
            return None

        key = auth_header.split(' ')[1]
        try:
            token = AdminToken.objects.get(key=key)
        except AdminToken.DoesNotExist:
            return None

        # Since AdminUser is not a standard User model, 
        # we return the AdminUser object and the token.
        return (token.admin, token)

    def authenticate_header(self, request):
        return 'Token'
