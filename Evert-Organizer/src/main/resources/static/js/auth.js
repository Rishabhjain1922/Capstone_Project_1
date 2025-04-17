const API_BASE = 'http://localhost:8080/api/auth';

// Login Handler for handle the login Request
async function handleLogin(event, role) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);

    try {
        // Clear any existing credentials
        await fetch(`${API_BASE}/logout`, {
            method: 'POST',
        });

        const response = await fetch(`${API_BASE}/login/${role}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                username: formData.get('username'),
                password: formData.get('password')
            }),
            credentials: 'include'
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Login failed');
        }

        const data = await response.json();
        localStorage.setItem(`${role}Username`, data.username); // Using response username
        localStorage.setItem('userRole', role);
        window.location.href = role === 'organizer' ? 'my-events.html' : 'attendee-events.html';
    } catch (error) {
        console.error('Login error:', error);
        showErrorAlert(error.message || 'Login failed. Please try again.');
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
});

// Show login form for specific role
function showLoginForm(role) {
    document.querySelectorAll('.login-form').forEach(form => form.classList.add('hidden'));
    document.getElementById(`${role}Login`).classList.remove('hidden');
}