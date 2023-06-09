from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.options import Options
import time
import os
import shutil
import sys
import boto3

print("cralwer.py debugging log 1")

# Setup webdriver
s=Service(ChromeDriverManager().install(), chrome_type='chromium')

print("cralwer.py debugging log 2")

chrome_options = Options()

print("cralwer.py debugging log 3")

# Define your two directories
download_directory = 'tmp_downloads'

# Set the download directory (replace '/path/to/download/directory' with your directory)
chrome_options.add_experimental_option('prefs',  {
    "download.default_directory": download_directory,
    "download.prompt_for_download": False,
    "download.directory_upgrade": True,
    "plugins.always_open_pdf_externally": True
    })

print("cralwer.py debugging log 4")

driver = webdriver.Chrome(service=s, options=chrome_options)

print("cralwer.py debugging log 5")

# Navigate to the login page
driver.get('https://soundraw.io/users/sign_in')

print("cralwer.py debugging log 6")

# Find the username and password fields (you'll need to inspect the page to find the right selectors)
username_field = driver.find_element(By.ID, 'email-sign-in')

print("cralwer.py debugging log 7")

password_field = driver.find_element(By.ID, 'password-sign-in')

print("cralwer.py debugging log 8")

# Enter your username and password
username_field.send_keys('fleur75921@gmail.com')

print("cralwer.py debugging log 9")

password_field.send_keys('a101ssafy')

print("cralwer.py debugging log 10")

# Find the login button and click it
login_button = driver.find_element(By.CSS_SELECTOR, 'input.btn.btn-yellow.w-100.text-font-semi-bold')

print("cralwer.py debugging log 11")

login_button.click()

print("cralwer.py debugging log 12")

# Wait for the login to complete (you may need to adjust this)
time.sleep(5)

print("cralwer.py debugging log 13")

# Check if you're logged in by looking for an element that's only present when you're logged in
# If you're not logged in, stop the script (you could also add code to retry the login)
if not driver.find_elements(By.ID, 'profile-btn'):
    print('Login failed')
    driver.quit()

print("cralwer.py debugging log 14")

if len(sys.argv) > 1:
    url = sys.argv[1]
    driver.get(url)

    print("cralwer.py debugging log 14-1")
else:
    # Navigate to the download page (replace with the URL of the download page)
    driver.get('https://soundraw.io/edit_music?length=60&tempo=normal,high,low&mood=Epic')
    print("cralwer.py debugging log 14-2")

# Wait for the login to complete (you may need to adjust this)
time.sleep(20)

print("cralwer.py debugging log 15")

# Find the download button (you'll need to inspect the page to find the right selector)
download_button = driver.find_element(By.CSS_SELECTOR, 'svg.fa-arrow-down')

print("cralwer.py debugging log 16")

# Click the download button
download_button.click()

print("cralwer.py debugging log 17")

# Wait for the download to start (you may need to adjust this)
time.sleep(60)

print("cralwer.py debugging log 18")

print("cralwer.py debugging log 19")

# Check if a file has been downloaded
while not os.listdir(download_directory):
    print('Waiting for download...')
    time.sleep(1)  # Wait for 1 second

print("cralwer.py debugging log 20")

# Get the name of the downloaded file
downloaded_file = os.listdir(download_directory)[0]

print("cralwer.py debugging log 21")

if len(sys.argv) > 2:
    new_name = f"{sys.argv[2]}.wav"
    print("cralwer.py debugging log 21-1")
else:
    # Rename and move the file
    new_name = 'new_music.wav'  # Replace with the new name and extension
    print("cralwer.py debugging log 21-2")
os.rename(os.path.join(download_directory, downloaded_file), os.path.join(download_directory, new_name))

print("cralwer.py debugging log 22")

# Check if a file with the same name already exists in the destination directory
counter = 1

print("cralwer.py debugging log 23")
base_name, extension = os.path.splitext(new_name)
print("cralwer.py debugging log 24")

while os.path.exists(os.path.join(destination_directory, new_name)):
    new_name = f"{base_name}_{counter}{extension}"  # Append a number to the filename
    print("cralwer.py debugging log 25")
    counter += 1
    print("cralwer.py debugging log 26")

# Rename the file in the download directory with the new name
os.rename(os.path.join(download_directory, f"{base_name}{extension}"), os.path.join(download_directory, new_name))
print("cralwer.py debugging log 27")

# Move the file
shutil.move(os.path.join(download_directory, new_name), os.path.join(destination_directory, new_name))
print("cralwer.py debugging log 28")

# Close the driver
driver.quit()
print("cralwer.py debugging log 29")
