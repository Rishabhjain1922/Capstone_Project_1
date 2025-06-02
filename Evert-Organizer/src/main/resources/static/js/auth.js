const API_BASE = 'https://capstone-project-1-dcit.onrender.com';
function storeToken(token) {
    localStorage.setItem('jwtToken', token);
}

// Get token from localStorage
function getToken() {
    return localStorage.getItem('jwtToken');
}

// Remove token from localStorage
function removeToken() {
    localStorage.removeItem('jwtToken');
}

// Login Handler for handle the login Request
// Login Handler
async function handleLogin(event, role) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);

    try {
        // Clear any existing token
        removeToken();

        const response = await fetch(`${API_BASE}/login/${role}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                username: formData.get('username'),
                password: formData.get('password')
            })
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Login failed');
        }

        const data = await response.json();
        storeToken(data.token);
        localStorage.setItem(`${role}Username`, data.username);
        localStorage.setItem('userRole', role);
        window.location.href = role === 'organizer' ? 'my-events.html' : 'attendee-events.html';
    } catch (error) {
        console.error('Login error:', error);
        showErrorAlert(error.message || 'Login failed. Please try again.');
    }
}

// For authenticated requests, add this function
async function makeAuthenticatedRequest(url, method = 'GET', body = null) {
    const token = getToken();
    if (!token) {
        throw new Error('No authentication token found');
    }

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };

    const config = {
        method,
        headers
    };

    if (body) {
        config.body = JSON.stringify(body);
    }

    const response = await fetch(url, config);
    if (response.status === 401) {
        // Token expired or invalid
        removeToken();
        window.location.href = 'login.html';
        throw new Error('Session expired. Please login again.');
    }

    if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Request failed');
    }

    return response;
}
// Logout Handler
async function handleLogout() {
    try {
        await fetch(`${API_BASE}/logout`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        });
    } catch (error) {
        console.error('Logout error:', error);
    } finally {
        removeToken();
        localStorage.removeItem('organizerUsername');
        localStorage.removeItem('attendeeUsername');
        localStorage.removeItem('userRole');
        window.location.href = 'login.html';
    }
}

// Registration Handler for handle the registration Request
async function handleRegistration(event, role) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);

    // Convert FormData to object
    const data = {};
    formData.forEach((value, key) => {
        data[key] = value;
    });

    try {
        showLoader(form);
        const response = await fetch(`${API_BASE}/register/${role}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Registration failed');
        }

        showSuccessAlert('Registration successful! Redirecting to login...');
        setTimeout(() => window.location.href = 'login.html', 1500);
    } catch (error) {
        showErrorAlert(error.message);
    } finally {
        hideLoader(form, 'Create Account');
    }
}

// Loader for showing the loadiing after submit
function showLoader(form) {
    const button = form.querySelector('button[type="submit"]');
    if (!button) return;

    const originalText = button.innerHTML;
    button.dataset.originalText = originalText;
    button.innerHTML = `
        <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
        Processing...
    `;
    button.disabled = true;
}


// Hide loader after the request is completed
function hideLoader(form, text) {
    const button = form.querySelector('button[type="submit"]');
    if (!button) return;

    button.innerHTML = button.dataset.originalText || text;
    button.disabled = false;
}


// Show success alert using SweetAlert2
function showSuccessAlert(message) {
    Swal.fire({
        icon: 'success',
        title: 'Success!',
        text: message,
        timer: 2000,
        showConfirmButton: false
    });
}


// Show error alert using SweetAlert2
function showErrorAlert(message) {
    Swal.fire({
        icon: 'error',
        title: 'Error!',
        text: message,
        timer: 3000
    });
}

// Initialize password toggles after DOM loads
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.password-toggle').forEach(toggle => {
        toggle.addEventListener('click', (e) => {
            const inputGroup = e.target.closest('.input-group');
            if (!inputGroup) return;

            const input = inputGroup.querySelector('input[type="password"], input[type="text"]');
            if (!input) return;

            const type = input.getAttribute('type') === 'password' ? 'text' : 'password';
            input.setAttribute('type', type);
            e.target.classList.toggle('fa-eye-slash');
        });
    });

    // Show organizer login form by default if coming from registration
    if (window.location.search.includes('role=organizer')) {
        showLoginForm('organizer');
    }
    // Check for token expiration periodically
    setInterval(() => {
        const token = getToken();
        if (token) {
            // You can add more sophisticated token expiration checking here
            // For now, we'll just check if token exists
            console.log('Token check - still valid');
        }
    }, 5 * 60 * 1000); // Check every 5 minutes
});

// Show login form for specific role
function showLoginForm(role) {
    document.querySelectorAll('.login-form').forEach(form => form.classList.add('hidden'));
    document.getElementById(`${role}Login`).classList.remove('hidden');
}