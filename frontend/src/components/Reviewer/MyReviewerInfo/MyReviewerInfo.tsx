import { useState } from "react";
import { useMutation, useQuery } from "react-query";

import styled, { css } from "styled-components";

import { deleteReviewer } from "apis/reviewer";
import { getReviewer } from "apis/reviewer";
import Confirm from "components/Confirm/Confirm";
import Loading from "components/Loading/Loading";
import MyReviewerEdit from "components/Reviewer/MyReviewerEdit/MyReviewerEdit";
import Button from "components/shared/Button/Button";
import { Flex, FlexCenter, FlexEnd } from "components/shared/Flexbox/Flexbox";
import useAuthContext from "hooks/useAuthContext";
import useModalContext from "hooks/useModalContext";
import useRevalidate from "hooks/useRevalidate";
import useToastContext from "hooks/useToastContext";
import { COLOR } from "utils/constants/color";
import { QUERY_KEY } from "utils/constants/key";
import { CONFIRM, SUCCESS_MESSAGE } from "utils/constants/message";

const Inner = styled(Flex)`
  flex-direction: column;
  width: 55%;
  margin-bottom: 2.5rem;
  padding: 1.25rem;
  border-radius: ${({ theme }) => theme.common.shape.rounded};
  line-height: 1.5625rem;
  box-shadow: ${({ theme }) => theme.common.boxShadow.primary};
`;

interface OpenContent {
  open: boolean;
}

const ContentTitle = styled.div`
  min-width: 2.1875rem;
  margin-right: 1.25rem;
  color: ${COLOR.GRAY_600};
  font-weight: 700;
`;

const Content = styled.p<OpenContent>`
  display: inline-block;
  line-height: 1.5;
  white-space: break-spaces;
  ${({ open }) =>
    open
      ? css`
          overflow: hidden;
        `
      : css`
          overflow: hidden;
          height: 4.5em;
          text-overflow: ellipsis;
        `};
`;

interface Props {
  reviewerId: number;
}

const MyReviewerInfo = ({ reviewerId }: Props) => {
  const [openContent, setOpenContent] = useState(false);
  const { authCheck } = useAuthContext();

  const { open } = useModalContext();
  const toast = useToastContext();

  const { data, isLoading } = useQuery([QUERY_KEY.GET_REVIEWER, reviewerId], async () => {
    const response = await getReviewer(reviewerId);

    if (!response.isSuccess) {
      toast(response.error.errorMessage, { type: "error" });

      return;
    }

    return response.data;
  });

  const { revalidate } = useRevalidate();

  const deleteReviewerMutation = useMutation(() => {
    return revalidate(async () => {
      const response = await deleteReviewer();

      if (response.isSuccess) {
        toast(SUCCESS_MESSAGE.API.REVIEWER.DELETE);
        authCheck();
      }

      return response;
    });
  });

  if (deleteReviewerMutation.isLoading || isLoading) return <Loading />;

  return (
    <>
      {data && (
        <Inner>
          <div css={{ fontWeight: 700, marginBottom: "0.9375rem" }}>????????? ????????? ??????</div>
          <Flex css={{ flexDirection: "column" }}>
            <Flex css={{ width: "100%" }}>
              <ContentTitle>??????</ContentTitle>
              <p>{data.title}</p>
            </Flex>
            <Flex>
              <ContentTitle>??????</ContentTitle>
              <FlexCenter css={{ flexDirection: "column" }}>
                <Content open={openContent}>{data.content}</Content>
                {data.content.length > 300 && (
                  <Button
                    themeColor="secondary"
                    onClick={() => {
                      setOpenContent(!openContent);
                    }}
                  >
                    {openContent ? "??????" : "?????????"}
                  </Button>
                )}
              </FlexCenter>
            </Flex>
            <Flex>
              {data.techSpec.languages.length > 0 && (
                <>
                  <ContentTitle>??????</ContentTitle>
                  <Flex css={{ flexDirection: "column" }}>
                    <p css={{ paddingRight: "0.625rem" }}>
                      {data.techSpec.languages.length > 0 &&
                        `??????: ${[...data.techSpec.languages.map((language) => language.name)].join(", ")}`}
                    </p>
                    <p>
                      {data.techSpec.skills.length > 0 &&
                        `??????: ${[...data.techSpec.skills.map((skill) => skill.name)].join(", ")}`}
                    </p>
                  </Flex>
                </>
              )}
            </Flex>
          </Flex>
          <Flex>
            <ContentTitle>??????</ContentTitle>
            <div>{data.career}???</div>
          </Flex>
          <FlexEnd>
            <Button themeColor="secondary" onClick={() => open(<MyReviewerEdit reviewer={data} />)}>
              ??????
            </Button>
            <Button
              themeColor="secondary"
              onClick={() => {
                open(
                  <Confirm
                    title={CONFIRM.REVIEWER.DELETE}
                    onConfirm={() => {
                      deleteReviewerMutation.mutate();
                    }}
                  />
                );
              }}
            >
              ??????
            </Button>
          </FlexEnd>
        </Inner>
      )}
    </>
  );
};

export default MyReviewerInfo;
