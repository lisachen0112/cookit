import React from 'react'

const Pagination = ({ page, totalPages, isLastPage, goToFirstPage, goToPreviousPage, goToPage, goToNextPage, goToLastPage }) => {
  return (
    <div className="d-flex justify-content-center mt-3">
      <nav aria-label="Page navigation example">
        <ul className="pagination">
          <li className={`page-item ${page === 0 ? 'disabled' : ''}`}>
            <a
              onClick={goToFirstPage}
              className="page-link"
              href="javascript:void(0)"
              aria-label="First"
            >
              <i className="fa-solid fa-angles-left"></i>
            </a>
          </li>
          <li className={`page-item ${page === 0 ? 'disabled' : ''}`}>
            <a
              onClick={goToPreviousPage}
              className="page-link"
              href="javascript:void(0)"
              aria-label="Previous"
            >
              <i className="fa-solid fa-angle-left"></i>
            </a>
          </li>
          {Array.from({ length: totalPages }, (_, pageIndex) => (
            <li key={pageIndex} className={`page-item ${page === pageIndex ? 'active' : ''}`}>
              <a
                onClick={() => goToPage(pageIndex)}
                className="page-link"
                href="javascript:void(0)"
              >
                {pageIndex + 1}
              </a>
            </li>
          ))}
          <li className={`page-item ${isLastPage ? 'disabled' : ''}`}>
            <a
              onClick={goToNextPage}
              className="page-link"
              href="javascript:void(0)"
              aria-label="Next"
            >
              <i className="fa-solid fa-chevron-right"></i>
            </a>
          </li>
          <li className={`page-item ${isLastPage ? 'disabled' : ''}`}>
            <a
              onClick={goToLastPage}
              className="page-link"
              href="javascript:void(0)"
              aria-label="Last"
            >
              <i className="fa-solid fa-angles-right"></i>
            </a>
          </li>
        </ul>
      </nav>
    </div>
  )
}

export default Pagination