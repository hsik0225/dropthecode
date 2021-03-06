import { render, screen, fireEvent } from "__mock__/utils/testUtils";
import ReviewerSearch from "pages/ReviewerSearch/ReviewerSearch";

const { queryByText, getByRole, findAllByRole, findByRole } = screen;

beforeEach(() => {
  render(<ReviewerSearch />);
});

describe("리뷰어찾기페이지 테스트", () => {
  it("리뷰어찾기 페이지 진입 후 언어 기술 목록을 조회 요청을 보내고 언어 기술 목록을 조회하면 언어 필터링 버튼을 찾을 수 있다.", async () => {
    const filteringbuttonBeforeLoaded = queryByText(/javascript/);

    expect(filteringbuttonBeforeLoaded).not.toBeInTheDocument();

    const filteringbuttonAfterLoaded = await findByRole("button", { name: /javascript/ });
    expect(filteringbuttonAfterLoaded).toBeInTheDocument();
  });

  it("언어필터링 버튼을 선택하면 선택된 언어에 맞는 기술 버튼들이 화면에 나타난다.", async () => {
    const skillButtonBeforeLoaded = queryByText(/react/);
    expect(skillButtonBeforeLoaded).not.toBeInTheDocument();

    const languageButton = await findByRole("button", { name: /javascript/ });
    fireEvent.click(languageButton);

    const matchedSkillButton = getByRole("button", { name: "react" });
    expect(matchedSkillButton).toBeInTheDocument();

    const notMatchedSkillButton = queryByText(/django/);
    expect(notMatchedSkillButton).not.toBeInTheDocument();
  });

  it("언어를 선택하면 리뷰어 목록이 나타난다.", async () => {
    const languageButton = await findByRole("button", { name: /javascript/ });
    fireEvent.click(languageButton);

    const reviewers = await findAllByRole("img");
    expect(reviewers).toHaveLength(10);
  });

  it("기술을 선택하면 리뷰어 목록이 나타난다.", async () => {
    const languageButton = await findByRole("button", { name: /javascript/ });
    fireEvent.click(languageButton);

    const skillButton = getByRole("button", { name: "vue" });
    fireEvent.click(skillButton);

    const reviewerImages = await findAllByRole("img");
    expect(reviewerImages).toHaveLength(10);
  });

  it("리뷰어 목록이 나타난 후 리뷰어를 추가로 조회할 수 있는 페이지네이션 버튼이 나타난다.", async () => {
    const languageButton = await findByRole("button", { name: /javascript/ });
    fireEvent.click(languageButton);

    const skillButton = getByRole("button", { name: "vue" });
    fireEvent.click(skillButton);

    const reviewerImages = await findAllByRole("img");
    expect(reviewerImages).toHaveLength(10);

    const paginationButton = getByRole("button", { name: "2" });
    expect(paginationButton).toBeInTheDocument();
  });
});
