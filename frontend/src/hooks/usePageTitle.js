import { useEffect } from "react";

const usePageTitle = (title) => {
  useEffect(() => {
    document.title = `${title} | PM SaaS`;
  }, [title]);
};

export default usePageTitle;
