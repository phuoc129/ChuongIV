*** Settings ***
Documentation    Test suite for login functionality of OrangeHRM
Library    SeleniumLibrary
Resource    resources/login_keywords.resource
Test Setup    Open Browser To Login Page
Test Teardown    Close Browser

*** Test Cases ***
Valid Login
    [Documentation]    Test login with valid credentials
    Input Username    Admin
    Input Password    admin123
    Click Login Button
    Dashboard Page Should Be Open
    
Invalid Login With Wrong Password
    [Documentation]    Test login with invalid password
    Input Username    Admin
    Input Password    wrong_password
    Click Login Button
    Error Message Should Be Visible    Invalid credentials
    
Empty Username Login
    [Documentation]    Test login with empty username
    Input Username    ${EMPTY}
    Input Password    admin123
    Click Login Button
    Required Field Message Should Be Visible    Username

Empty Password Login
    [Documentation]    Test login with empty password
    Input Username    Admin
    Input Password    ${EMPTY}
    Click Login Button
    Required Field Message Should Be Visible    Password