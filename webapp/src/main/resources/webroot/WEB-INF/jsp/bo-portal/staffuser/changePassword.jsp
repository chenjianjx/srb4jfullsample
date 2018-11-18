<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

      <div class="card card-login mx-auto mt-5">
        <div class="card-header">Change Password</div>
        <div class="card-body">

          <form action="/bo/portal/staffusers/admin/change-password" method="post">

            <div class="form-group">
                <%@ include file="/WEB-INF/jsp/bo-portal/common/form-errors.jsp" %>
            </div>

            <c:if test="${it.changeReason != null}">
                <div class="form-group">
                    <div class="text-info"> <c:out value="${it.changeReason}"/> </div>
                </div>
            </c:if>

            <div class="form-group">
              <div class="form-label-group">
                <input type="text" id="username" name="username" value="${sessionStaffUser.username}"
                    class="form-control" placeholder="Username" required="required" disabled="true">
                <label for="username">Username</label>
              </div>
            </div>

            <div class="form-group">
              <div class="form-label-group">
                <input type="password" id="currentPassword" name="currentPassword" value="${it.currentPassword}"
                    class="form-control" placeholder="Current password" required="required" autofocus="autofocus">
                <label for="currentPassword">Current Password</label>
              </div>
            </div>

            <div class="form-group">
              <div class="form-label-group">
                <input type="password" id="newPassword" name="newPassword" value="${it.newPassword}"
                    class="form-control" placeholder="New password" required="required" >
                <label for="newPassword">New password</label>
              </div>
            </div>

            <div class="form-group">
              <div class="form-label-group">
                <input type="password" id="confirmPassword" name="confirmPassword" value="${it.confirmPassword}"
                    class="form-control" placeholder="Confirm password" required="required" >
                <label for="confirmPassword">Confirm password</label>
              </div>
            </div>


            <input class="btn btn-primary btn-block" value="Submit" type="submit"/>
          </form>
        </div>
      </div>