from rest_framework.authentication import BaseAuthentication
from rest_framework.exceptions import AuthenticationFailed
from .models import CustomerToken

class CustomerTokenAuthentication(BaseAuthentication):
    def authenticate(self, request):
        auth_header = request.headers.get('Authorization')
        if not auth_header:
            return None

        if not auth_header.startswith('Token '):
            return None

        key = auth_header.split(' ')[1]
        try:
            token = CustomerToken.objects.get(key=key)
        except CustomerToken.DoesNotExist:
            raise AuthenticationFailed('Invalid token')

        return (token.user, token)

    def authenticate_header(self, request):
        return 'Token'
