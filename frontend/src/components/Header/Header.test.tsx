import { mockingStudentAuth, mockingTeacherAuth, mockingAnonymousAuth } from "__mock__/utils/mockingAuth";
import { render, screen } from "__mock__/utils/testUtils";

import Header from "./Header";

const { queryByRole, findByRole } = screen;

describe("헤더 컴포넌트 테스트", () => {
  it("로그인 하지 않은 경우 로그인 버튼이 렌더링 된다.", async () => {
    mockingAnonymousAuth();
    render(<Header />);

    const loginButton = await findByRole("button", { name: "로그인" });
    expect(loginButton).toBeInTheDocument();

    const logoutButton = queryByRole("button", { name: "로그아웃" });
    expect(logoutButton).not.toBeInTheDocument();
  });
  it("학생인 경우 리뷰어 등록하기 버튼이 렌더링 된다.", async () => {
    mockingStudentAuth();
    render(<Header />);

    const reviewerRegisterButton = await findByRole("link", { name: "리뷰어 등록하기" });
    expect(reviewerRegisterButton).toBeInTheDocument();
  });

  it("리뷰어인 경우 리뷰어 등록하기 버튼이 렌더링 되지 않는다.", async () => {
    mockingTeacherAuth();
    render(<Header />);

    const reviewerRegisterButton = await findByRole("link", { name: "리뷰어 등록하기" });

    expect(reviewerRegisterButton).not.toBeInTheDocument();
  });
});
