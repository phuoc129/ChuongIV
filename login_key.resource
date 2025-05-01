*** Settings ***
Documentation    Tài nguyên và từ khóa cho các bài kiểm tra đăng nhập OrangeHRM
Library    SeleniumLibrary

*** Variables ***
${SERVER}               opensource-demo.orangehrmlive.com
${BROWSER}              chrome
${LOGIN_URL}            https://${SERVER}/web/index.php/auth/login
${DASHBOARD_URL}        https://${SERVER}/web/index.php/dashboard/index
${USERNAME_FIELD}       css:input[name="username"]
${PASSWORD_FIELD}       css:input[name="password"]
${LOGIN_BUTTON}         css:button[type="submit"]
${ERROR_MESSAGE}        css:.oxd-alert-content-text
${REQUIRED_MESSAGE}     css:.oxd-input-field-error-message

*** Keywords ***
Mở Trình Duyệt Đến Trang Đăng Nhập
    [Documentation]    Mở trình duyệt và điều hướng đến trang đăng nhập
    Open Browser    ${LOGIN_URL}    ${BROWSER}
    Maximize Browser Window
    Wait Until Element Is Visible    ${USERNAME_FIELD}
    Page Should Contain Element    ${LOGIN_BUTTON}

Nhập Tên Người Dùng
    [Arguments]    ${username}
    [Documentation]    Nhập tên người dùng vào trường tên đăng nhập
    Input Text    ${USERNAME_FIELD}    ${username}

Nhập Mật Khẩu
    [Arguments]    ${password}
    [Documentation]    Nhập mật khẩu vào trường mật khẩu
    Input Text    ${PASSWORD_FIELD}    ${password}

Nhấn Nút Đăng Nhập
    [Documentation]    Nhấn nút đăng nhập
    Click Element    ${LOGIN_BUTTON}

Trang Dashboard Nên Hiển Thị
    [Documentation]    Xác minh rằng trang dashboard đã mở sau khi đăng nhập thành công
    Wait Until Location Contains    ${DASHBOARD_URL}
    Page Should Contain    Dashboard
    Element Should Be Visible    css:.oxd-topbar-header-title

Thông Báo Lỗi Nên Hiển Thị
    [Arguments]    ${expected_message}
    [Documentation]    Xác minh rằng thông báo lỗi được hiển thị với văn bản dự kiến
    Wait Until Element Is Visible    ${ERROR_MESSAGE}
    Element Should Contain    ${ERROR_MESSAGE}    ${expected_message}

Thông Báo Trường Bắt Buộc Nên Hiển Thị
    [Arguments]    ${field}
    [Documentation]    Xác minh rằng thông báo trường bắt buộc được hiển thị
    Wait Until Element Is Visible    ${REQUIRED_MESSAGE}
    Element Should Contain    ${REQUIRED_MESSAGE}    Required