import urllib.request
import urllib.error
import json

BASE_URL = "http://127.0.0.1:8000/api/customer"

def test_login():
    print("Testing Login...")
    url = f"{BASE_URL}/login/"
    data = {
        "email": "test@example.com",
        "password": "wrongpassword"
    }
    json_data = json.dumps(data).encode("utf-8")
    
    req = urllib.request.Request(url, data=json_data, headers={'Content-Type': 'application/json'})
    
    try:
        with urllib.request.urlopen(req) as response:
            print(f"Status Code: {response.getcode()}")
            print("Response Content:")
            print(response.read().decode('utf-8'))
    except urllib.error.HTTPError as e:
        print(f"HTTP Error Code: {e.code}")
        print("Response Content:")
        print(e.read().decode('utf-8')[:2000])
    except Exception as e:
        print(f"Request failed: {e}")

if __name__ == "__main__":
    test_login()
