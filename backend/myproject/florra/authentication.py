from rest_framework.authentication import BaseAuthentication
from rest_framework.exceptions import AuthenticationFailed
from .models import CustomerToken

class CustomerTokenAuthentication(BaseAuthentication):
    def authenticate(self, request):
        # Check both standard header and META for maximum compatibility
        auth_header = request.headers.get('Authorization') or request.META.get('HTTP_AUTHORIZATION')
        
        if not auth_header:
            return None

        # Support both Token and Bearer prefixes to be robust
        parts = auth_header.split(' ')
        if len(parts) != 2:
            return None
            
        prefix = parts[0].lower()
        if prefix not in ['token', 'bearer']:
            return None

        key = parts[1]
        try:
            token = CustomerToken.objects.get(key=key)
        except CustomerToken.DoesNotExist:
            return None

        return (token.user, token)

    def authenticate_header(self, request):
        return 'Token'
